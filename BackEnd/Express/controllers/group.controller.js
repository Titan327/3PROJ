const Group = require('../models/group.model');
const Joi = require('joi');
const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET;

const createGroup = async (req, res) => {
    console.log(`REST createGroup`);
    const schema = Joi.object({
        name: Joi.string().min(3).max(63).required(),
        description: Joi.string().min(3).max(255),
    });

    const { error, value } = schema.validate(req.body, { abortEarly: false });
    const { name, description } = value;

    console.log(error);
    if (error) {
        const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
        return res.status(400).json({ error: errorMessage });
    }

    try {
        const token = req.headers.authorization.split(' ')[1];
        const decodedToken = jwt.verify(token, JWT_SECRET);
        console.log(decodedToken);
        const user_id = decodedToken.user_id;

        await Group.create({
            name,
            description,
            owner_id: user_id,
        });
        res.status(201).send({ message: "Group created successfully"});
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

module.exports = {
    createGroup
}