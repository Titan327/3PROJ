const UserGroup = require("../models/userGroup.model");
const Transactions = require("../models/transaction.model");
const {minioClient} = require("../configurations/minio.config");
const path = require("path");



const postTicket = async (req, res) => {
    try {
        const userId = req.authorization.userId;
        const groupId = req.params.groupId;
        const transactionId = req.params.transactionId;

        if (!await UserGroup.findOne({ where: { userId: userId, groupId: groupId } })) {
            return res.status(403).send({ error: "You are not in this group" });
        }

        await minioClient.putObject("ticket", groupId+'/'+transactionId/*+extension*/, req.file.buffer);

        await Transactions.update({
                //receipt:process.env.APP_URL+"api/img/ticket/"+groupId+"/"+transactionId
                receipt:true
            },{
            where:{
                id:transactionId
            }
        })

        return res.status(200).send({ message: "Ticket uploaded successfully"});

    }catch (e){
        console.log(e)
        return res.status(500).send({ error: "Internal error server" });
    }
}

const getTicketByName = async (req, res) => {
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
    postTicket,
    getTicketByName
}