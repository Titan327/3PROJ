const {genSalt, hash} = require("bcrypt");
const User = require('../models/user.model');
const UserGroup = require('../models/userGroup.model');
const UserController = require('./userGroup.controller');
const UserGroupController = require('./userGroup.controller');
const TransactionUserController = require('./transactionUser.controller');
const Joi = require("joi");
const UserMiddleware = require('../middlewares/user.middleware');
const GroupController = require('./group.controller');
const {where} = require("sequelize");
const {CreateNotif} = require("./notif.controller");

const getUser = async (req, res) => {
    try {
        const user = await User.findByPk(req.params.userId, {
            attributes: ['id', 'firstname', 'lastname', 'username', 'email', 'birth_date', 'profile_picture'],
        });
        if (user === null) {
            return res.status(404).send('User not found');
        }
        const base_url = user.profile_picture;
        if (base_url) {
            user.profile_picture = [base_url+"/100",base_url+"/200",base_url+"/500"]
        } else {
            user.profile_picture = null;
        }
        return res.status(200).send(user);
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const createUser = async (req, res) => {

    const { lastname, firstname, username, email, birth_date, password } = req.value;
    try {
        const salt = await genSalt(12);
        const passwordHash = await hash(password, salt);
        await User.create({
            firstname,
            lastname,
            username,
            email,
            birth_date,
            password: passwordHash
        });
        res.status(201).send({ message: "User creates successfully"});
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const modifyUser = async (req, res) => {

    const { lastname, firstname, username, email, birth_date } = req.value;
    const user = await User.findOne({ where: { id: req.authorization.userId } });


    if (email !== user.email){
        if (await User.findOne({ where: { email: email } }) !== null) {
            return res.status(409).send({ message: "Email already taken"});
        }
        await UserMiddleware.verifyPasswordForSensibleInfos(req, res, () => {});
    }
    if (username !== user.username && await User.findOne({ where: { username: username } }) !== null) {
        return res.status(409).send({ message: "Username already taken"});
    }
    try {
        await User.update({
            firstname,
            lastname,
            username,
            email,
            birth_date
        }, {
            where: { id: req.authorization.userId }
        });
        return res.status(201).send({ message: "User modified successfully"});
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const modifyPassword = async (req, res) => {

    const schema = Joi.object({
        password: Joi.string().min(8).max(20).regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/).required(),
        passwordConfirm: Joi.ref('password')
    });
    if (req.body.userInfos == null) {
        return res.status(400).send({ message: "User infos are required" });
    }
    const { error, value } = schema.validate(req.body.userInfos, { abortEarly: false });
    if (error) {
        const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
        return res.status(400).json({ error: errorMessage });
    }
    const { password } = value;
    const user = await User.findOne({ where: { id: req.authorization.userId } });
    try {
        const salt = await genSalt(12);
        const passwordHash = await hash(password, salt);
        await user.update({
            password: passwordHash
        });
        await CreateNotif(req.authorization.userId,`Votre mot de passe à été modifié`,`user-data`);
        res.status(201).send({ message: "Password modified successfully"});
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const deleteUser = async (req, res) => {

    try {
        const userId = req.params.userId;
        await User.update({
            firstname: `DeletedUser ${userId}`,
            lastname: `DeletedUser ${userId}`,
            username: `DeletedUser ${userId}`,
            email: `DeletedUser ${userId}`,
            birth_date: new Date(),
            password: `DeletedUser ${userId}`,
            profile_picture: null
        }, {
            where: { id: userId }
        });
        await UserController.deleteUserGroupRelationsWhenDeletingUser(req.params.userId);
        await UserGroupController.deleteUserGroupRelationsWhenDeletingUser(req.params.userId);
        await GroupController.switchGroupOwnerWhenDeletingUser(req.params.userId);
        res.status(200).send({ message: "User deleted successfully"});
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const getAmountOfAllUserTransactionsThisMonth = async (req, res) => {

    try {
        const transactions = await TransactionUserController.getAllUserTransactionsThisMonth(req.params.userId);
        let amount = 0;
        for (let i = 0; i < transactions.length; i++) {
            amount += transactions[i].amount;
        }
        return res.status(200).send({ amount: amount, transactions: transactions.length });
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const totalBalance = async (req, res) => {

    try {
        const userGroups = await UserGroup.findAll({
            where: { userId: req.params.userId },
            attributes: ['balance']
        });
        let amount = 0;
        for (let i = 0; i < userGroups.length; i++) {
            if (userGroups[i].balance < 0) {
                amount += userGroups[i].balance;
            }
        }
        return req.res.status(200).send({ amount: amount });
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

module.exports = {
    getUser,
    createUser,
    modifyUser,
    modifyPassword,
    deleteUser,
    getAmountOfAllUserTransactionsThisMonth,
    totalBalance
}