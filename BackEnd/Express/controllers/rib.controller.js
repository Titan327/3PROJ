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

        const img = (req.file.buffer).toString('base64');
        await minioClient.putObject("rib", userId+'/'+idMethod, Security.crypt(process.env.AES_PAYEMENT_KEY,img));

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

        const paymentMethod = await PaymentMethode.findOne({ _id: idMethod, userId: userId });
        if (!paymentMethod) {
            return res.status(404).send({ error: "Payment method doesn't exist" });
        }

        let documentData = '';

        minioClient.getObject('rib', `${userId}/${idMethod}`, (err, dataStream) => {
            if (err) {
                if (err.code === 'NoSuchKey') {
                    return res.status(404).send({ error: "File doesn't exist" });
                } else {
                    console.error(err);
                    return res.status(500).send({ error: "Internal server error" });
                }
            }

            res.contentType('image/jpeg');

            dataStream.on('data', (chunk) => {
                documentData += chunk;
            });

            dataStream.on('end', () => {
                const decryptData = Security.decrypt(process.env.AES_PAYEMENT_KEY, documentData);
                res.write(Buffer.from(decryptData, 'base64'));
                res.end();
            });

            dataStream.on('error', (streamErr) => {
                console.error(streamErr);
                return res.status(500).send({ error: "Error reading the file stream" });
            });
        });

    } catch (e) {
        console.log(e);
        return res.status(500).send({ error: "Internal server error" });
    }
};


module.exports={
    postRib,
    getRibById
}