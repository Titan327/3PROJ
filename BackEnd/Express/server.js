require('dotenv').config();
const express = require('express');
const cors = require('cors');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const swaggerDoc = YAML.load('./swagger.yaml');
const http = require('http'); // Importation du module http
const socketIo = require('socket.io');
const path = require('path');
const { createTransport } = require("nodemailer");

const app = express();
const server = http.createServer(app); // Création du serveur HTTP
const io = socketIo(server);

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cors({
    origin: '*',
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
    credentials: true,
}));
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDoc));

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

const PORT = process.env.PORT;

server.listen(PORT, () => console.log(`Server up and running on http://localhost:${PORT}`));

// Importation des configurations et des routes
require('./configurations/db.config');
const Group = require('./models/group.model');
const User = require('./models/user.model');
const UserGroup = require('./models/userGroup.model');
const Transaction = require('./models/transaction.model');
const TransactionCategory = require('./models/transactionCategory.model');
const TransactionUser = require('./models/transactionUser.model');
const Invitation = require('./models/invitation.model');
const Refund = require('./models/refund.model');

app.use("", require("./routes/invitation.route"));
app.use("/api/auth", require("./routes/auth.route"));
app.use("/api/group", require("./routes/group.route"));
app.use("/api/user", require("./routes/user.route"));
app.use("/api/transaction", require("./routes/transaction.route"));
