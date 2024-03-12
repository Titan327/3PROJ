const {genSalt, hash} = require("bcrypt");
const User = require('../models/user.model');
const UserController = require('./userGroup.controller');

const getUser = async (req, res) => {
    try {
        const user = await User.findByPk(req.params.userId, {
            attributes: ['id', 'firstname', 'lastname', 'username', 'email', 'birth_date'], // specify the fields you want to retrieve
        });
        if (user === null) {
            return res.status(404).send('User not found');
        }
        return res.status(200).send(user);
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const createUser = async (req, res) => {
    console.log(`REST createUser`);
    console.log(req.value);
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
    console.log(`REST modifyUser`);
    const { lastname, firstname, username, email, birth_date, password } = req.value;
    const user = await User.findOne({ where: { id: req.authorization.userId } });
    if (email !== user.email && await User.findOne({ where: { email: email } }) !== null) {
        return res.status(409).send({ message: "Email already taken"});
    }
    if (username !== user.username && await User.findOne({ where: { username: username } }) !== null) {
        return res.status(409).send({ message: "Username already taken"});
    }
    try {
        const salt = await genSalt(12);
        const passwordHash = await hash(password, salt);
        await User.update({
            firstname,
            lastname,
            username,
            email,
            birth_date,
            password: passwordHash
        }, {
            where: { id: req.authorization.userId }
        });
        res.status(201).send({ message: "User modified successfully"});
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const deleteUser = async (req, res) => {
    console.log(`REST deleteUser`);
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
        res.status(200).send({ message: "User deleted successfully"});
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

module.exports = {
    getUser,
    createUser,
    modifyUser,
    deleteUser
}