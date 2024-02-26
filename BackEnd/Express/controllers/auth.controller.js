const Joi = require("joi");
const Token = require('../security/token.security')
const User = require("../models/user.model");
const {genSalt, hash, compare} = require("bcrypt");
const transporter = require("../configurations/email.config");
const path = require('path');

const fs = require('fs');
const {isTokenValid} = require("../security/token.security");

const ForgottenPasswordTemplate = path.join(__dirname, '../Email_template/ForgottenPassword.html');


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
}

const authentication = async (req, res) => {
    console.log(`REST login`);
    let credentialsAreValid = false;
    let user = null;
    if (req.body.email) {
        credentialsAreValid = await loginWithEmail(req.body.email, req.body.password);
        user = await User.findOne({where: { email: req.body.email }});
    }
    else if (req.body.username) {
        credentialsAreValid = await loginWithUsername(req.body.username, req.body.password);
        user = await User.findOne({where: { username: req.body.username }});
    }
    if (credentialsAreValid){
        const token = Token.createToken(user);
        return res.status(200).send(token);
    }
    return res.status(400).send("Credentials incorrect");
}

const forgottenPassword = async (req, res) => {

    try {
        const schema = Joi.object({
            email: Joi.string().email().required(),
        });
        const { error, value } = schema.validate(req.body, { abortEarly: false });
        const { email } = value;

        if (error) {
            const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
            return res.status(400).json({ error: errorMessage });
        }

        const user = await User.findOne({
            where: {
                email: email,
            },
        });

        if (!user){
            return res.status(449).send({error:"Mail not found"});
        }

        const token = Token.createToken(user,'1h');
        console.log(token);

        let htmlContent = fs.readFileSync(ForgottenPasswordTemplate,'utf8');
        htmlContent = htmlContent.replace('{{link}}', "https://youtube.com"); //rediriger ver le form du front

        let mailOptions = {
            from: 'contact@tristan-tourbier.com',
            to: email,
            subject: 'Email oublié',
            html: htmlContent
        };

        await transporter.sendMail(mailOptions, function(error, info){
            if (error) {
                return res.status(500).send({error:"Internal Server Error"});
            } else {
                return res.status(200).send({message:"Mail send"});
            }
        });
    }catch (err){
        return res.status(500).json({ error: "Une erreur interne au serveur est surenue, veuiller contacter les administrateurs" });
    }
}

const resetPassword = async (req, res) => {
    try {

        const tokenValue = req.authorization

        const schema = Joi.object({
            password: Joi.string().min(8).max(20).regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/).required(),
            passwordConfirm: Joi.ref('password')
        });
        const { error, value } = schema.validate(req.body, { abortEarly: false });
        const { password } = value;

        if (error) {
            const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
            return res.status(400).json({ error: errorMessage });
        }

        const salt = await genSalt(12);
        const passwordHash = await hash(password, salt);

        await User.update({
                password : passwordHash
            },
            {where: {id : tokenValue.user_id}}
        ).then(() => {
            return res.status(200).json({ success: "Mot de passe modifié" });
        })

    }catch (err){
        return res.status(500).json({ error: "Une erreur interne au serveur est surenue, veuiller contacter les administrateurs" });
    }
}


function loginWithEmail(email, password) {
    console.log("login with email");
    return new Promise((resolve, reject) => {
        User.findOne({ where: { email: email } })
            .then(async user => {
                if (!user) {
                    resolve(false); // Utilisateur non trouvé, informations d'identification invalides
                } else {
                    const isPasswordValid = await compare(password, user.password)
                        .then(isPasswordValid => {
                            resolve(isPasswordValid); // Résoudre avec la validité du mot de passe
                        })
                        .catch(e => reject(e));
                }
            })
            .catch(e => reject(e));
    });
}

function loginWithUsername(username, password) {
    console.log("login with username");
    return new Promise((resolve, reject) => {
        User.findOne({ where: { username: username } })
            .then(async user => {
                if (!user) {
                    resolve(false); // Utilisateur non trouvé, informations d'identification invalides
                } else {
                    const isPasswordValid = await compare(password, user.password)
                        .then(isPasswordValid => {
                            resolve(isPasswordValid); // Résoudre avec la validité du mot de passe
                        })
                        .catch(e => reject(e));
                }
            })
            .catch(e => reject(e));
    });
}

module.exports = {
    register,
    authentication,
    forgottenPassword,
    resetPassword
}