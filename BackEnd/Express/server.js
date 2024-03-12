require('dotenv').config();
const express = require('express');
const cors = require('cors');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const http = require('http'); // Importez le module http
const socketIo = require('socket.io');
const path = require('path');
const { createTransport } = require("nodemailer");

const app = express();
const apiPort = process.env.API_PORT || 3000; // Port pour l'API
const chatPort = process.env.CHAT_PORT || 9000; // Port pour le chat

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cors({
    origin: '*',
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
    credentials: true,
}));
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(YAML.load('./swagger.yaml')));

// Importation des configurations et des routes
require('./configurations/db.config');
const Group = require('./models/group.model');
const User = require('./models/user.model');
const UserGroup = require('./models/userGroup.model');
const Transaction = require('./models/transaction.model');
const TransactionCategory = require('./models/transactionCategory.model');
const TransactionUser = require('./models/transactionUser.model');
const Invitation = require('./models/invitation.model');

app.use("", require("./routes/invitation.route"));
app.use("/api/auth", require("./routes/auth.route"));
app.use("/api/group", require("./routes/group.route"));
app.use("/api/user", require("./routes/user.route"));
app.use("/api/transaction", require("./routes/transaction.route"));

// Création du serveur HTTP pour l'API
const server = http.createServer(app);
const io = socketIo(server);

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

// Serveur pour l'API
server.listen(apiPort, () => console.log(`API Server up and running on http://localhost:${apiPort}`));

// Serveur pour le chat
app.get("/chat", function(req, res){
    res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(chatPort, () => console.log(`Chat Server up and running on http://localhost:${chatPort}`));
