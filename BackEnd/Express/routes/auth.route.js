const router = require('express').Router();
const AuthController = require('../controllers/auth.controller');
const securityMiddleware = require("../security/middleware.security");
const UserMiddleware = require('../middlewares/user.middleware');

//PUBLIC
//POST http://localhost:9002/api/auth/register
router.post("/register", UserMiddleware.verifyUserInfos ,AuthController.register);
//POST http://localhost:9002/api/auth/login
router.post("/login",AuthController.authentication);
//POST http://localhost:9000/api/auth/forgottenPassword
router.post("/forgottenPassword",AuthController.forgottenPassword);
//GET http://localhost:9000/api/auth/resetPassword
router.get("/resetPassword",securityMiddleware.verifyIsAuth,AuthController.resetPassword);


module.exports = router;
