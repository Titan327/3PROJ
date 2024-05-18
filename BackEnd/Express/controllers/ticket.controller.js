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

        const img = (req.file.buffer).toString('base64');
        await minioClient.putObject("ticket", groupId+'/'+transactionId/*+extension*/, img);

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

        let documentData = '';

        await minioClient.getObject("ticket", groupId+'/'+transactionId, (err, dataStream) => {
            if (err) {
                return res.status(404).json({ error: "Picture not found" });
            }

            dataStream.on('data', (chunk) => {
                documentData += chunk;
            });

            dataStream.on('end', async () => {
                try {
                    const { fileTypeFromBuffer } = await import('file-type');
                    const file_buff = Buffer.from(documentData, 'base64');
                    const type = await fileTypeFromBuffer(file_buff);

                    console.log(type);

                    if (type) {
                        res.set('Content-Type', type.mime);
                    } else {
                        res.set('Content-Type', 'application/octet-stream');
                    }

                    res.write(file_buff);
                    res.end();
                } catch (error) {
                    console.error('Error processing the file:', error);
                    res.status(500).send('Internal Server Error');
                }
            });

            dataStream.on('error', (streamErr) => {
                console.error(streamErr);
                return res.status(500).send({ error: "Error reading the file stream" });
            });


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