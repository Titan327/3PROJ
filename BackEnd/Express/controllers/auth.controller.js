const Joi = require("joi");
const Token = require('../security/token.security')
const User = require("../models/user.model");
const {genSalt, hash, compare} = require("bcrypt");


const register = async (req, res) => {
    console.log(`REST register`);
    const schema = Joi.object({
        lastname: Joi.string().min(3).max(63).required(),
        firstname: Joi.string().min(3).max(63).required(),
        username: Joi.string().min(3).required(),
        email: Joi.string().email().max(100).required(),
        birth_date: Joi.date().required(),
        password: Joi.string().min(8).max(20).regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/).required(),
        passwordConfirm: Joi.ref('password')
    });
    const { error, value } = schema.validate(req.body, { abortEarly: false });
    const { lastname, firstname, username, email, birth_date, password } = value;

    if (error) {
        const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
        return res.status(400).json({ error: errorMessage });
    }

    const usernameIsAlreadyInDb = await User.findOne({where: { "username": username }});
    const emailIsAlreadyInDb = await User.findOne({where: { email: email }});
    if (usernameIsAlreadyInDb) {
        return res.status(409).send({ message: "Username already taken"});
    }
    if (emailIsAlreadyInDb) {
        return res.status(409).send({ message: "Email already taken"});
    } else {
        try {
            //hasher le password
            const salt = await genSalt(12);
            const passwordHash = await hash(password, salt);
            console.error(firstname, lastname, username, email, birth_date, passwordHash)
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
}

const authentication = async (req, res) => {
    const { pseudo, password } = req.body;
    console.log(`REST authentication user: ${pseudo}`);
    try {
        const userDB = await User.findOne({ pseudo });
        if (!userDB) {
            res.status(400).send("Credentials incorrect");
        } else {
            const isPasswordValid = await compare(password, userDB.password);
            if (!isPasswordValid) {
                res.status(400).send("Credentials incorrect");
            } else {
                const token = Token.createToken(userDB);
                res.status(200).send(token);
            }
        }
    } catch (e) {
        console.error(e)
        res.status(500).send(e);
    }
}

module.exports = {
    register,
    authentication
}