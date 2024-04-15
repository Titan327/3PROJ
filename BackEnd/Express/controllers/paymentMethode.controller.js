const PaymentMethode = require('../models/paymentMethode.model');
const UserGroup = require('../models/userGroup.model');

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
            return res.status(200).send(paymentMethodes);
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
        } else if(PaymentMethode.findOne({ where: { userId: userId, type: type, value: value } })) {
            return res.status(409).send({ error: "Payment methode already exists" });
        }
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

module.exports = {
    getPaymentMethode,
    createPaymentMethode
}
