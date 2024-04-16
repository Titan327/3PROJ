const PaymentMethode = require('../models/paymentMethode.model');
const UserGroup = require('../models/userGroup.model');
const crypto = require('crypto');

const getPaymentMethode = async (req, res) => {
    console.log(`REST getPaymentMethode`);
    const groupId = req.params.groupId;
    const userToSend = req.params.userId;
    const userAsking = req.authorization.userId;
    try {
        if (!await UserGroup.findOne({ where: { userId: userAsking, groupId: groupId } })) {
            return res.status(403).send({ error: "You are not in this group" });
        } else if (!await UserGroup.findOne({ where: { userId: userToSend, groupId: groupId } })) {
            return res.status(404).send({ error: "User not found in this group" });
        } else {
            const paymentMethodes = await PaymentMethode.findAll({
                where: {
                    userId: userToSend,
                }
            });
            if (paymentMethodes === null) {
                return res.status(404).send({ error: "Payment methode not found" });
            }

            //console.log(typeof paymentMethodes)

            var result = [];

            paymentMethodes.forEach(
                methode => {
                    const crypt = methode.value.split(':');
                    // recup iv
                    const iv = Buffer.from(crypt.shift(), 'hex');
                    const encryptedText = Buffer.from(crypt.join(':'), 'hex');
                    // decrypt
                    const decipher = crypto.createDecipheriv('aes-256-cbc', Buffer.from(process.env.AES_PAYEMENT_KEY,'hex'), iv);
                    let decrypted = decipher.update(encryptedText);
                    decrypted = Buffer.concat([decrypted, decipher.final()]);
                    const decrypt = decrypted.toString();

                    result.push({[methode.type]:decrypt});
                }
            )

            return res.status(200).send(result);
        }
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const createPaymentMethode = async (req, res) => {
    console.log(`REST createPaymentMethode`);
    const userId = req.authorization.userId;
    let { type, value } = req.body;
    value = value.replace(/\s/g, '');
    try {
        if (!type || !value) {
            return res.status(400).send({ error: "Missing type or value" });
        } else if(await PaymentMethode.findOne({ where: { userId: userId, type: type, value: value } })) {
            return res.status(409).send({ error: "Payment methode already exists" });
        }

        const key = process.env.AES_PAYEMENT_KEY;
        console.log('Longueur de la clé:', key.length);
        console.log('Type de la clé:', typeof key);

        // creation de iv (comme le salt)
        const iv = crypto.randomBytes(16);
        // creation du chiffreur (cipher)
        const cipher = crypto.createCipheriv('aes-256-cbc', Buffer.from(process.env.AES_PAYEMENT_KEY,'hex'), iv);
        // chiffrage
        let encrypted = cipher.update(value);
        encrypted = Buffer.concat([encrypted, cipher.final()]);
        value = iv.toString('hex') + ':' + encrypted.toString('hex');

        await PaymentMethode.create({
            userId,
            type,
            value
        });
        res.status(201).send({ message: "Payment methode created successfully" });
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const test = async (req, res) => {
    const aesKey = crypto.randomBytes(32);

    console.log('Clé AES générée:', aesKey.toString('hex'));

    res.status(200).send("ok")
}

module.exports = {
    getPaymentMethode,
    createPaymentMethode,
    test,
}
