const UserGroup = require('../models/userGroup.model');
const Refund = require('../models/refund.model');
const {Op, where} = require("sequelize");

const processRefund = async (req, res) => {
    console.log(`REST refundTransactions`);
    const groupId = req.params.groupId;
    const refundId = req.params.refundId;
    try {
        let refund = await Refund.findOne({where: {id: refundId}});
        if (!refund) {
            return res.status(404).send('Refund not found');
        }
        if (refund.groupId !== groupId) {
            return res.status(400).send('This refund does not belong to this group');
        }
        if (refund.processed) {
            return res.status(400).send('Refund already processed');
        }
        let {refundingUserId, refundedUserId, amount} = refund;
        let refundingUser = await UserGroup.findOne({where: {groupId, userId: refundingUserId}});
        let refundedUser = await UserGroup.findOne({where: {groupId, userId: refundedUserId}});

        await Refund.update({processed: true}, {where: {id: refundId}});

        await UserGroup.update({balance: refundingUser.balance + amount}, {where: {groupId, userId: refundingUserId}});
        await UserGroup.update({balance: refundedUser.balance - amount}, {where: {groupId, userId: refund.refundedUserId}});

        await calculateMinimalRefunds(groupId)
        return res.status(200).send('Refund processed');
    } catch (error) {
        console.error(error);
        return res.status(500).send('Internal Server Error');
    }
}

const getGroupRefunds = async (req, res) => {
    console.log(`REST getGroupRefunds`);
    const {groupId} = req.params;
    try {
        let refunds = await Refund.findAll({
            where: {groupId, processed: false},
            attributes: ['id', 'refundingUserId', 'refundedUserId', 'amount']
        });
        return res.status(200).send(refunds);
    } catch (error) {
        console.error(error);
        return res.status(500).send('Internal Server Error');
    }
}

const calculateMinimalRefunds = async (groupId) => {
    try {
        console.log(`REST calculateMinimalRefunds`);

        await Refund.destroy({where: {groupId, processed: false}});
        let users = await UserGroup.findAll({
            where: { groupId, active: true , balance: { [Op.ne]: 0 }},
            attributes: ['userId', 'balance']
        });

        let negativeUsers = users.filter(user => user.balance < 0);
        let positiveUsers = users.filter(user => user.balance > 0);

        for (let negativeUser of negativeUsers) {
            let positUserExact = positiveUsers.find(user => user.balance === -negativeUser.balance);
            if (positUserExact) {
                await Refund.create({
                    groupId,
                    refundingUserId: negativeUser.userId,
                    refundedUserId: positUserExact.userId,
                    amount: -negativeUser.balance,
                    processed: false
                });
                positUserExact.balance = 0;
                negativeUser.balance = 0;
                negativeUsers = negativeUsers.filter(user => user.balance !== 0);
                positiveUsers = positiveUsers.filter(user => user.balance !== 0);
            }
        }

        negativeUsers.sort((a, b) => b.balance - a.balance);
        positiveUsers.sort((a, b) => b.balance - a.balance);

        for (let negatifUser of negativeUsers) {
            for (let positifUser of positiveUsers) {
                if (-negatifUser.balance >= positifUser.balance) {
                    if (positifUser.balance !== 0) {
                        await Refund.create({
                            groupId,
                            refundingUserId: negatifUser.userId,
                            refundedUserId: positifUser.userId,
                            amount: positifUser.balance,
                            processed: false
                        });
                        negatifUser.balance += positifUser.balance;
                        positifUser.balance = 0;
                    }
                }
            }
        }

        //Si il reste des négatifs et des positifs, on rembourse les plus gros possibles
        for (let negatifUser of negativeUsers) {
            for (let positifUser of positiveUsers) {
                if (-negatifUser.balance !== 0) {
                    await Refund.create({
                        groupId,
                        refundingUserId: negatifUser.userId,
                        refundedUserId: positifUser.userId,
                        amount: -negatifUser.balance,
                        processed: false
                    });
                    positifUser.balance += negatifUser.balance;
                    negatifUser.balance = 0;
                }
            }
        }
    } catch (error) {
        console.error(error);
        throw error;
    }
}


module.exports = {
    processRefund,
    getGroupRefunds,
}