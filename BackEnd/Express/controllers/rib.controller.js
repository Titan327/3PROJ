const UserGroup = require("../models/userGroup.model");
const {minioClient} = require("../configurations/minio.config");
const path = require("path");
const PaymentMethode = require('../models/paymentMethode.model');
const Security = require('../security/AES.security');


const postRib = async (req, res) => {
    try {
        const userId = req.authorization.userId;
        const idMethod = req.params.idMethod;

        if(!await PaymentMethode.findOne({_id: idMethod,userId: userId})){
            return res.status(404).send({ error: "Payement method don't exist" });
        }

        await minioClient.putObject("rib", userId+'/'+idMethod, req.file.buffer);
        //await minioClient.putObject("rib", userId+'/'+idMethod, req.file.buffer);

        return res.status(200).send({ message: "RIB uploaded successfully"});

    }catch (e){
        console.log(e)
        return res.status(500).send({ error: "Internal error server" });
    }
}

const getRibById = async (req, res) => {
    try {
        const userId = req.authorization.userId;
        const idMethod = req.params.idMethod;

        if(!await PaymentMethode.findOne({_id: idMethod,userId: userId})){
            return res.status(404).send({ error: "Payement method don't exist" });
        }

        minioClient.getObject('rib', userId+'/'+idMethod, (err, data) => {

        });

    }catch (e) {
        console.log(e)
        return res.status(500).send({error: "Internal error server"});
    }
}

module.exports={
    postRib,
    getRibById
}