const router = require('express').Router();
const AuthController = require('../controllers/auth.controller');
const securityMiddleware = require("../security/middleware.security");

//PUBLIC
//POST http://localhost:9000/api/auth/register
router.post("/register", AuthController.register);
//POST http://localhost:9000/api/auth/login
router.post("/login",AuthController.authentication);
//POST http://localhost:9000/api/auth/forgottenPassword
router.post("/forgottenPassword",AuthController.forgottenPassword);
//GET http://localhost:9000/api/auth/resetPassword
router.get("/resetPassword",securityMiddleware.verifyIsAuth,AuthController.resetPassword);


module.exports = router;
