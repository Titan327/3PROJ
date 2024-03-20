const Joi = require("joi");
const User = require("../models/user.model");
const {compare} = require("bcrypt");

const verifyUserInfos = async (req, res, next) => {
    console.log(`REST verifyUserInfos`);
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
    const { error, value } = schema.validate(req.body.userInfos, { abortEarly: false });
    if (error) {
        const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
        return res.status(400).json({ error: errorMessage });
    }

    req.value = value;
    next();
}

const verifyPasswordForSensibleInfos = async (req, res, next) => {
    console.log(`REST verifyPasswordForSensibleInfos`);
    if (!req.body.password) {
        return res.status(400).send({ message: "Password is required for this operation" });
    }
    const user = await User.findOne({ where: { id: req.params.userId } });
    if (!await compare(req.body.password, user.password)) {
        return res.status(401).send({ message: "Unauthorized" });
    }
    next();
}

const verifyPasswordForSensibleInfosWhenModifying = async (req, res, next) => {
    console.log(`REST verifyPasswordForSensibleInfos`);
    if (!req.body.password) {
        return res.status(400).send({ message: "Password is required for this operation" });
    }
    const user = await User.findOne({ where: { id: req.params.userId } });
    if (user.email === req.body.userInfos.email) {
        if (!await compare(req.body.password, user.password)) {
            return res.status(401).send({message: "Unauthorized"});
        }
    }
    next();
}

module.exports = {
    verifyUserInfos,
    verifyPasswordForSensibleInfos,
    verifyPasswordForSensibleInfosWhenModifying
}