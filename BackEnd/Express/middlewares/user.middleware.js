const Joi = require("joi");
const User = require("../models/user.model");
const {compare} = require("bcrypt");

const verifyUserInfos = async (req, res, next) => {

    const schema = Joi.object({
        lastname: Joi.string().min(3).max(63).required(),
        firstname: Joi.string().min(3).max(63).required(),
        username: Joi.string().min(3).required(),
        email: Joi.string().email().max(100).required(),
        birth_date: Joi.date().required(),
    });
    if (req.body.userInfos == null) {
        return res.status(400).send({ message: "User infos are required" });
    }
    const { lastname, firstname, username, email, birth_date } = req.body.userInfos;
    const userInfos = { lastname, firstname, username, email, birth_date };
    const { error, value } = schema.validate(userInfos, { abortEarly: false });
    if (error) {
        const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
        return res.status(400).json({ error: errorMessage });
    }

    req.value = value;
    next();
}

const verifyPasswordWhenCreatingOrUpdating = async (req, res, next) => {

    const schema = Joi.object({
        password: Joi.string().min(8).max(20).regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/).required(),
        passwordConfirm: Joi.ref('password')
    });
    if (req.body.userInfos == null) {
        return res.status(400).send({ message: "User infos are required" });
    }
    const { password, passwordConfirm } = req.body.userInfos;
    const passwordInfos = { password, passwordConfirm };
    const { error, value } = schema.validate(passwordInfos, { abortEarly: false });
    if (error) {
        const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
        return res.status(400).json({ error: errorMessage });
    }

    req.value = {...req.value, ...value};
    next();
}

const verifyPasswordForSensibleInfos = async (req, res, next) => {

    if (!req.body.password) {
        return res.status(400).send({ message: "Password is required for this operation" });
    }
    const user = await User.findOne({ where: { id: req.params.userId } });
    if (!await compare(req.body.password, user.password)) {
        return res.status(401).send({ message: "Unauthorized" });
    }
    next();
}

module.exports = {
    verifyUserInfos,
    verifyPasswordWhenCreatingOrUpdating,
    verifyPasswordForSensibleInfos
}