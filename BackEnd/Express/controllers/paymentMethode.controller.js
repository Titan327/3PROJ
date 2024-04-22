const PaymentMethode = require('../models/paymentMethode.model');
const UserGroup = require('../models/userGroup.model');
const crypto = require('crypto');
const Joi = require('joi');

const getPaymentMethode = async (req, res) => {
    const groupId = req.params.groupId;
    const userToSend = req.params.userId;
    const userAsking = req.authorization.userId;
    try {
        if (!await UserGroup.findOne({ where: { userId: userAsking, groupId: groupId } })) {
            return res.status(403).send({ error: "You are not in this group" });
        } else if (!await UserGroup.findOne({ where: { userId: userToSend, groupId: groupId } })) {
            return res.status(404).send({ error: "User not found in this group" });
        } else {
            const paymentMethodes = await PaymentMethode.find({
                userId: userToSend,
            });
            if (paymentMethodes === null) {
                return res.status(404).send({ error: "Payment methode not found" });
            }

            let result = [];
            let obj;

            paymentMethodes.forEach(
                methode => {
                    obj = {
                        id: methode._id,
                        type: methode.type,
                        value: {}
                    };
                    Object.keys(methode.value).forEach(
                        val => {
                            obj.value[val] = decrypt(process.env.AES_PAYEMENT_KEY,methode.value[val]);
                        }
                    )
                    result.push(obj);
                }
            );

            return res.status(200).send(result);
        }
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const createPaymentMethode = async (req, res) => {
    const userId = req.authorization.userId;
    let data = { type, paypal_username, name, surname, bank_name, box_code, bank_number, account_number, RIB_key, IBAN, BIC } = req.body;
    //value = value.replace(/\s/g, '');
    try {

        if (!type) {
            return res.status(400).send({ error: "Missing type" });
        }

        let all;
        let alreadyExist = false;

        if (type === "RIB"){

            all = await PaymentMethode.find({ userId: userId, type: type });
            all.forEach(
                RIB_crypt => {
                    const decrypt_IBAN = decrypt(process.env.AES_PAYEMENT_KEY,RIB_crypt.value.IBAN);
                    if (IBAN === decrypt_IBAN){
                        alreadyExist = true;
                    }
                }
            );

        }else if (type === "Paypal"){
            all = await PaymentMethode.find({ userId: userId, type: type });
            all.forEach(
                paypal_crypt => {
                    const decrypt_paypal = decrypt(process.env.AES_PAYEMENT_KEY,paypal_crypt.value.user_paypal);
                    if (paypal_username === decrypt_paypal){
                        alreadyExist = true;
                    }
                }
            );
        }

        if (alreadyExist){
            return res.status(409).send({ error: "Payment methode already exists" });
        }

        let result = {};

        if (type === "Paypal"){
            console.log(data);
            const schema = Joi.object({
                type: Joi.string().required(),
                paypal_username: Joi.string().required()
            });
            const { error } = schema.validate(data);
            if (error) {
                return res.status(449).send({ error: error.message });
            }
            if (paypal_username.charAt(0) === '@') {
                paypal_username.slice(1);
            }
            result.user_paypal = paypal_username;
        }else if (type === "RIB"){
            const schema = Joi.object({
                type: Joi.string().required(),
                name: Joi.string().required(),
                surname: Joi.string().required(),
                bank_name: Joi.string().required(),
                bank_number: Joi.string().length(5).required(),
                box_code: Joi.string().length(5).required(),
                account_number: Joi.string().length(11).required(),
                RIB_key: Joi.string().length(2).required(),
                IBAN: Joi.string().regex(/FR[a-zA-Z0-9]{2}\s?([0-9]{4}\s?){2}([0-9]{2})([a-zA-Z0-9]{2}\s?)([a-zA-Z0-9]{4}\s?){2}([a-zA-Z0-9]{1})([0-9]{2})\s?/).message('Le format de l\'IBAN est invalide')

            });

            const { error } = schema.validate(data);
            if (error) {
                return res.status(449).send({ error: error.message });
            }
            delete data.type;
            result = data;
        }else {
            return res.status(449).send({ error: "The payement methode can only be a paypal mail or a French RIB" });
        }

        let result_crypt = {
            userId:userId,
            type:type,
            value:{},
        }

        Object.keys(result).forEach(
            detail => {
                result_crypt.value[detail] = crypt(process.env.AES_PAYEMENT_KEY,result[detail]);
            }
        );

        await PaymentMethode.create(result_crypt);
        return res.status(201).send({ message: "Payment methode created successfully" });

    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const test = async (req, res) => {
    const aesKey = crypto.randomBytes(32);

    console.log('Clé AES générée:', aesKey.toString('hex'));

    res.status(200).send("ok")
}

const getMyPaymentMethode = async (req,res) => {
    const userId = req.authorization.userId;
    try {
        const paymentMethodes = await PaymentMethode.find({
            userId: userId,
        });
        if (paymentMethodes === null) {
            return res.status(404).send({ error: "Payment methode not found" });
        }

        let result = [];
        let obj;

        paymentMethodes.forEach(
            methode => {
                obj = {
                    id: methode._id,
                    type: methode.type,
                    value: {}
                };
                Object.keys(methode.value).forEach(
                    val => {
                        obj.value[val] = decrypt(process.env.AES_PAYEMENT_KEY,methode.value[val]);
                    }
                )
                result.push(obj);
            }
        );

        return res.status(200).send(result);
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

function crypt(key,string){
    // creation de iv (comme le salt)
    const iv = crypto.randomBytes(16);
    // creation du chiffreur (cipher)
    const cipher = crypto.createCipheriv('aes-256-cbc', Buffer.from(key,'hex'), iv);
    // chiffrage
    let encrypted = cipher.update(string);
    encrypted = Buffer.concat([encrypted, cipher.final()]);
    return iv.toString('hex') + ':' + encrypted.toString('hex');
}


function decrypt(key,string){

    const crypt = string.split(':');
    // recup iv
    const iv = Buffer.from(crypt.shift(), 'hex');
    const encryptedText = Buffer.from(crypt.join(':'), 'hex');
    // decrypt
    const decipher = crypto.createDecipheriv('aes-256-cbc', Buffer.from(key,'hex'), iv);
    let decrypted = decipher.update(encryptedText);
    decrypted = Buffer.concat([decrypted, decipher.final()]);
    return decrypted.toString();

}

module.exports = {
    getPaymentMethode,
    createPaymentMethode,
    test,
    getMyPaymentMethode
}
