const http = require('http');
const WebSocket = require('ws');
const { spawn } = require('child_process');

const PORT = process.env.PORT || 8080;
const GAME_DIR = __dirname;

const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'text/plain' });
  res.end('Gangland Game Server\n');
});

const wss = new WebSocket.Server({ server });

wss.on('connection', (ws, req) => {
  console.log(`Player connected from ${req.socket.remoteAddress}`);

  const game = spawn('java', ['-cp', GAME_DIR, 'Gangland'], {
    cwd: GAME_DIR,
  });

  game.stdout.on('data', (data) => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(data.toString());
    }
  });

  game.stderr.on('data', (data) => {
    console.error('Game stderr:', data.toString());
  });

  game.on('close', (code) => {
    console.log(`Game process exited with code ${code}`);
    if (ws.readyState === WebSocket.OPEN) {
      ws.close();
    }
  });

  game.on('error', (err) => {
    console.error('Failed to start game process:', err.message);
    if (ws.readyState === WebSocket.OPEN) {
      ws.send('\r\n[Server error: could not start game process]\r\n');
      ws.close();
    }
  });

  ws.on('message', (message) => {
    if (game.stdin.writable) {
      game.stdin.write(message.toString() + '\n');
    }
  });

  ws.on('close', () => {
    console.log('Player disconnected — killing game process');
    game.kill('SIGTERM');
  });

  ws.on('error', (err) => {
    console.error('WebSocket error:', err.message);
    game.kill('SIGTERM');
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Gangland server listening on port ${PORT}`);
});
