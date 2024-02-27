const Token = require('../security/token.security')
const User = require("../models/user.model");
const UserController = require("../controllers/user.controller");
const {compare} = require("bcrypt");


const register = async (req, res) => {
    console.log(`REST register`);
    const {username, email } = req.body.userInfos;

    const usernameIsAlreadyInDb = await User.findOne({where: { username: username }});
    const emailIsAlreadyInDb = await User.findOne({where: { email: email }});
    if (usernameIsAlreadyInDb) {
        return res.status(409).send({ message: "Username already taken"});
    }
    if (emailIsAlreadyInDb) {
        return res.status(409).send({ message: "Email already taken"});
    } else {
        await UserController.createUser(req, res);
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
    authentication
}