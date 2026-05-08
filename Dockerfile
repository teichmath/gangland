FROM eclipse-temurin:21-jdk

# Install Node.js 20
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Compile Java sources
COPY src/ ./src/
RUN mkdir out && javac -d out src/*.java

# Install Node.js dependencies and server
COPY package.json ./
RUN npm install --production
COPY server.js ./

EXPOSE 8080

CMD ["node", "server.js"]
