require('dotenv').config();
const express = require('express');
const cors = require('cors');
const swaggerUi = require('swagger-ui-express');
const swaggerJsdoc = require('swagger-jsdoc');
YAML = require('yamljs');
swaggerDoc = YAML.load('./swagger.yaml');
const app = express();
const {createTransport} = require("nodemailer");



app.use(express.json());
app.use(express.urlencoded({extended:false}));
app.use(cors({
    origin:'*',
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
    credentials: true,
}))


app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDoc));



const PORT = process.env.PORT;

app.listen(PORT, () => console.log(`Server up and running on http://localhost:${PORT}`))

require('./configurations/db.config');

const Group = require('./models/group.model');
const User = require('./models/user.model');
const UserGroup = require('./models/userGroup.model');
const Transaction = require('./models/transaction.model');
const TransactionCategory = require('./models/transactionCategory.model');
const TransactionUser = require('./models/transactionUser.model');


app.use("/api/auth", require("./routes/auth.route"));
app.use("/api/group", require("./routes/group.route"));
app.use("/api/user", require("./routes/user.route"));
app.use("/api/transaction", require("./routes/transaction.route"));