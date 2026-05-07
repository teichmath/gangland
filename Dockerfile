FROM eclipse-temurin:21-jdk

# Install Node.js 20
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy game source files.
# Excludes duplicate-class files (Gangland_B, Gangland_cant_see_it, conflicted copy)
# and standalone test files (math_floor_test, stringtest1) that aren't part of the game.
COPY Attack.java \
     Beast.java \
     Being.java \
     Biography.java \
     Butterfly.java \
     Cat.java \
     Creeper.java \
     Devil.java \
     Dice.java \
     Dog.java \
     Dragon.java \
     Gangland.java \
     Gnome.java \
     Grue.java \
     Human.java \
     Pet.java \
     Pig.java \
     Player.java \
     Rat.java \
     Rattishman.java \
     Rumor.java \
     Safeland.java \
     Skeleton.java \
     Snake.java \
     Spider.java \
     Springland.java \
     Tool.java \
     Tree.java \
     Wait.java \
     Wererat.java \
     ./

RUN javac *.java

# Install Node.js dependencies
COPY package.json ./
RUN npm install --production

COPY server.js ./

EXPOSE 8080

CMD ["node", "server.js"]
