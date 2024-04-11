const Message = require('../models/message.model');
const UserGroup = require('../models/userGroup.model');

const Post = async (req,res) => {

    const { text } = req.body;
    const { groupId } = req.params;
    const userId = req.authorization.userId;

    const isInGroup = await UserGroup.findOne({where: {userId: userId}});

    if (isInGroup){
        const nouveauMessage = new Message({
            text: text,
            userId: userId,
            groupId: groupId
        });

        nouveauMessage.save()
            .then(savedMessage => {
                console.log("Message enregistré avec succès :", savedMessage);
                return res.status(200).json({ message: 'Message saved' });
            })
            .catch(error => {
                console.error("Erreur lors de l'enregistrement du message :", error);
                return res.status(500).json({ message: 'Message not send' });
            });
    }else {
        return res.status(403).json({ message: 'User not in group' });
    }

}

const Get = async (req,res) => {

    const { groupId } = req.params;
    var { limit,page } = req.query;
    page = page - 1;

    if ( limit > 0 && page >= 0){
        Message
            .find({groupId: groupId})
            .skip(limit*page).limit(limit)
            .sort({ timestamp: -1 })
            .then(messages => {

                if (messages.length > 0){
                    return res.status(200).json({ messages });
                }else {
                    return res.status(449).json({ message: 'End of message history' });
                }


        }).catch(err => {
            return res.status(500).json({ message: 'Internal error server' });
        });
    }else {
        return res.status(449).json({ message: 'page or limit should be >= 0' });
    }

}

const Delete = async (req,res) => {

    const { messageId } = req.query

    Message.findByIdAndDelete(messageId)
        .then(removedMessage => {
            if (removedMessage) {
                return res.status(200).json({ message: 'Message deleted' });
            } else {
                return res.status(404).json({ message: 'Message not found' });
            }
        })
        .catch(err => {
            return res.status(500).json({ message: 'Internal error server' });
        });

}

module.exports={
    Post,
    Get,
    Delete
}