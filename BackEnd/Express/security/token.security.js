const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET;

const createToken = (user) => {
    return jwt.sign(
        {
            userId: user.id
        },
        JWT_SECRET,
        {expiresIn: '24h'}
    )
}

const isTokenValid = (token) => {
    try {
        return jwt.verify(token, JWT_SECRET);
    } catch (e) {
        console.error("Error verifying token: ", e.message);
        return false;
    }
}


module.exports = {
    createToken,
    isTokenValid
}