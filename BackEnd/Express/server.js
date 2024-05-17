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
const {initializeBucket} = require("./configurations/minio.config");

const app = express();
const server = http.createServer(app); // Création du serveur HTTP
const io = socketIo(server, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"],
        credentials: true
    }
});


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

    socket.on('chat message', function (msg, group){
        io.emit(`chat-group-${group}`, msg, group);
    });
    socket.on('private message', function (msg, group, user2, username, user1){
        io.emit(`chat-private-${group}`, msg, group);
        CreateNotif(user2,`Nouveau message privé de ${username}`,`groups/${group}/private-chat/${user1}`);
    });

    socket.on('new-transaction', function (group){
        io.emit(`new-transaction-${group}`);
    });
});
app.post('/api/messages', (req, res) => {
    const { message } = req.body;
    // Diffuser le message à tous les utilisateurs connectés
    io.emit('chat message', message);
    res.status(200).send('Message sent successfully');
});



const PORT = process.env.PORT;

server.listen(PORT, () => console.log(`Server up and running on http://localhost:${PORT}`));

// Importation des configurations et des routes
require('./configurations/db.config');
const Group = require('./models/group.model');
const User = require('./models/user.model');
const UserGroup = require('./models/userGroup.model');
const Transaction = require('./models/transaction.model');
const TransactionUser = require('./models/transactionUser.model');
const TransactionCategory = require('./models/transactionCategory.model');
const Invitation = require('./models/invitation.model');
const Refund = require('./models/refund.model');

require('./configurations/mongo.config');
const Message = require('./models/message.model');
const PaymentMethode = require('./models/paymentMethode.model');
const BankInfo = require('./models/bankInfo.model');
const { CreateNotif } = require('./controllers/notif.controller');

app.use("", require("./routes/invitation.route"));
app.use("/api/auth", require("./routes/auth.route"));
app.use("/api/groups", require("./routes/group.route"));
app.use("/api/users", require("./routes/user.route"));
app.use("/api/transactionCategories", require("./routes/transactionCategory.route"));
app.use("/api/oauth2", require("./routes/oauth2.route"));
app.use("/api/img", require("./routes/image.route"));
app.use("/api/messages", require("./routes/message.route"));
app.use("/api/bankInfo", require("./routes/bankInfo.route"));
app.use("/api/notifs", require("./routes/notif.route"));

initializeBucket("pp-user");
initializeBucket("pp-group");
initializeBucket("ticket");
initializeBucket("rib");
