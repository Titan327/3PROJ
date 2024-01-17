const Joi = require('joi');
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const User = require('../models/Users');


async function CreateUser(req, res){

    try {

        const testRyan = {
            "l1":"on est la",
            "l2":"on est la",
            "l3":"on est la",
        }

        return res.status(418).json({ json: testRyan });

    } catch (error) {

        return res.status(500).json({ error: "error." });

    }

}

async function LoginUser(req,res){

    try {

        return res.status(418).json({ error: "on est la" });

    }catch (error){

    }

}


module.exports = {
    CreateUser,
    LoginUser
};