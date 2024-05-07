const UserGroup = require("../models/userGroup.model");
const {minioClient} = require("../configurations/minio.config");
const path = require("path");
const PaymentMethode = require('../models/paymentMethode.model');


const postRib = async (req, res) => {
    try {
        const userId = req.authorization.userId;
        const idMethod = req.params.idMethod;

        if(!await PaymentMethode.findOne({_id: idMethod,userId: userId})){
            return res.status(404).send({ error: "Payement method don't exist" });
        }

        await minioClient.putObject("rib", userId+'/'+idMethod, req.file.buffer);

        return res.status(200).send({ message: "RIB uploaded successfully"});

    }catch (e){
        console.log(e)
        return res.status(500).send({ error: "Internal error server" });
    }
}

const getRibById = async (req, res) => {
    try {
        const userId = req.authorization.userId;
        const groupId = req.params.groupId;
        const transactionId = req.params.transactionId;

        if (!await UserGroup.findOne({ where: { userId: userId, groupId: groupId } })) {
            return res.status(403).send({ error: "You are not in this group" });
        }

        await minioClient.getObject("ticket", groupId+'/'+transactionId, (err, dataStream) => {
            if (err) {
                return res.status(404).json({ error: "Picture not found" });
            }
            dataStream.pipe(res);
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