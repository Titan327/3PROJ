const router = require('express').Router();
const AuthController = require('../controllers/auth.controller');
const securityMiddleware = require("../security/middleware.security");
const UserMiddleware = require('../middlewares/user.middleware');
const PaymentMethodeController = require("../controllers/paymentMethode.controller");

//PUBLIC
//POST http://localhost:9002/api/auth/register
router.post("/register", UserMiddleware.verifyUserInfos , UserMiddleware.verifyPasswordWhenCreatingOrUpdating ,AuthController.register);
//POST http://localhost:9002/api/auth/login
router.post("/login",AuthController.authentication);
//POST http://localhost:9000/api/auth/forgottenPassword
router.post("/forgottenPassword",AuthController.forgottenPassword);
//GET http://localhost:9000/api/auth/resetPassword
router.post("/resetPassword",securityMiddleware.verifyIsAuth,AuthController.resetPassword);


module.exports = router;
