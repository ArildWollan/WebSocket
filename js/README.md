Prerequisites: Atom and node

1. Install npm packages:
```sh
npm install -g nodemon
```

2. Install JavaScript dependencies (settings file: package.json):
```sh
cd server
npm install
cd ..
```

3. Run nodemon server (restarts if the source files are changed) with newest JS standard (harmony) enabled:
```sh
nodemon --harmony server/server.js
```

4. Open http://localhost:3000
