const router = require('express').Router();
const AuthController = require('../controllers/auth.controller');
const UserMiddleware = require('../middlewares/user.middleware');

//PUBLIC
//POST http://localhost:9002/api/auth/register
router.post("/register", UserMiddleware.verifyUserInfos ,AuthController.register);
//POST http://localhost:9002/api/auth/login
router.post("/login",AuthController.authentication);


module.exports = router;
