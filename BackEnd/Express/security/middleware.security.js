const Token = require('./token.security')
const User = require('../models/user.model')

const verifyIsAuth = (req, res, next) => {
    console.log("Verifying token of the user \n");

    // header: authorization: Bearer token
    const header = req.headers.authorization;
    if (!header?.startsWith("Bearer ")) {
        return res.status(401).send({ message: "Unauthorized"});
    }

    // Récupère le token du header
    const jwt = header.split(" ")[1];
    const verifyToken = Token.isTokenValid(jwt);
    if (!verifyToken) {
        return res.status(403).send({ message: "Unauthorized"});
    }


    //Si il est bon
    req.authorization = verifyToken;
    next();
}

const verifyIsAuthAndActivUser = async (req, res, next) => {
    console.log("Verifying if the token is ok and if the user requesting is the actual user \n");

    verifyIsAuth(req, res, async () => {
        try {
            // Récupérer l'utilisateur depuis la base de données
            const user = await User.findOne({ where: { id: req.authorization.userId } });
            if (!user) {
                return res.status(403).send({ message: "Unauthorized" });
            }
            // Comparer les ID
            if (user.id.toString() !== req.params.userId) {
                return res.status(403).send({ message: "Unauthorized" });
            }
            // Si tout est bon
            next();
        } catch (error) {
            console.error(error);
            return res.status(500).send({ message: "Internal Server Error" });
        }
    });
};


module.exports = {
    verifyIsAuth,
    verifyIsAuthAndActivUser
}