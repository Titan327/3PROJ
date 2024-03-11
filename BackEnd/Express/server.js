require('dotenv').config();
const express = require('express');
const cors = require('cors');
const http = require('http');
const socketIo = require('socket.io');
const path = require('path');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const swaggerDoc = YAML.load('./swagger.yaml');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

const PORT = process.env.PORT || 3000;

// Middleware pour permettre l'envoi et la réception de données JSON
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

// Middleware pour autoriser les requêtes CORS
app.use(cors({
    origin: '*',
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
    credentials: true,
}));

// Middleware pour la documentation Swagger
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDoc));

// Routes
const AuthRoute = require("./routes/auth.route");
app.use("/api/auth", AuthRoute);

// Page d'accueil du chat
app.get("/chat", function(req, res){
    res.sendFile(path.join(__dirname, 'index.html'));
});

// Gestion des connexions WebSocket pour le chat
io.on('connection', function(socket){
    console.log('A user is connected');

    socket.on('disconnect', function (){
        console.log('A user is disconnected');
    });

    socket.on('chat message', function (msg){
        console.log('Message received: ' + msg);
        io.emit('chat message', msg);
    });
});

// Démarrage du serveur principal et de Socket.IO
server.listen(PORT, () => {
    console.log(`Server up and running on http://localhost:${PORT}`);
});
