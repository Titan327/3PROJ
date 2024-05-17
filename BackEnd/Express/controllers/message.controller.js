const Message = require('../models/message.model');
const UserGroup = require('../models/userGroup.model');
const User = require('../models/user.model');

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
    const userId = req.authorization.userId;

    const isInGroup = await UserGroup.findOne({where: {userId: userId}});


    if (isInGroup){
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
    }else {
        return res.status(403).json({ message: 'User not in group' });
    }

}

const Delete = async (req,res) => {

    const { messageId } = req.query

    const userId = req.authorization.userId;
    const isInGroup = await UserGroup.findOne({where: {userId: userId}});

    if (isInGroup){
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
    }else {
        return res.status(403).json({ message: 'User not in group' });
    }
}

const GetUserToUser = async (req,res) => {

    let { groupId,user1,user2 } = req.params;
    var { limit,page } = req.query;
    page = page - 1;

    const userId = req.authorization.userId;
    if (userId === user1 || userId === user2){
        const isInGroup = await UserGroup.findOne({where: {groupId: groupId}});

        if (isInGroup){

            const user1Exist = await User.findOne({where: {id: user1}});
            const user2Exist = await User.findOne({where: {id: user2}});

            if (user1Exist && user2Exist){

                if (user1 > user2){
                    [user1, user2] = [user2, user1];
                }

                if ( limit > 0 && page >= 0){
                    Message
                        .find({groupId: groupId+"/"+user1+"/"+user2})
                        .skip(limit*page).limit(limit)
                        .sort({ timestamp: -1 })
                        .then(messages => {

                            if (messages.length > 0){
                                return res.status(200).json({ messages });
                            }else {
                                return res.status(449).json({ message: 'End of message history' });
                            }

                        }).catch(err => {
                            console.log(err);
                            return res.status(500).json({ message: 'Internal error server' });
                    });
                }else {
                    return res.status(449).json({ message: 'page or limit should be >= 0' });
                }

            }else {
                return res.status(449).json({ message: 'User dont exist' });
            }

        }else {
            return res.status(403).json({ message: 'User not in group' });
        }
    }else {
        return res.status(403).json({ message: "You can only request your messages" });
    }
}

const PostUserToUser = async (req,res) => {

    let { groupId,user1,user2 } = req.params;
    const { text } = req.body;

    const userId = req.authorization.userId;
    if (userId === user1 || userId === user2){
        const isInGroup = await UserGroup.findOne({where: {groupId: groupId}});

        if (isInGroup){

            const user1Exist = await User.findOne({where: {id: user1}});
            const user2Exist = await User.findOne({where: {id: user2}});

            if (user1Exist && user2Exist){

                if (user1 > user2){
                    [user1, user2] = [user2, user1];
                }

                const nouveauMessage = new Message({
                    text: text,
                    userId: userId,
                    groupId: groupId+"/"+user1+"/"+user2
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
                return res.status(449).json({ message: 'User dont exist' });
            }

        }else {
            return res.status(403).json({ message: 'User not in group' });
        }
    }else {
        return res.status(403).json({ message: "You can only request your messages" });
    }
}

const DeleteUserToUser = async (req,res) => {

    let { groupId,user1,user2 } = req.params;
    const { messageId } = req.query

    const userId = req.authorization.userId;
    if (userId === user1 || userId === user2){
        const isInGroup = await UserGroup.findOne({where: {groupId: groupId}});

        if (isInGroup){

            const user1Exist = await User.findOne({where: {id: user1}});
            const user2Exist = await User.findOne({where: {id: user2}});

            if (user1Exist && user2Exist){

                if (user1 > user2){
                    [user1, user2] = [user2, user1];
                }

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


            }else {
                return res.status(449).json({ message: 'User dont exist' });
            }

        }else {
            return res.status(403).json({ message: 'User not in group' });
        }
    }else {
        return res.status(403).json({ message: "You can only request your messages" });
    }

}


module.exports={
    Post,
    Get,
    Delete,
    GetUserToUser,
    PostUserToUser,
    DeleteUserToUser
}