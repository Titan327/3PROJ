const router = require('express').Router();
const AuthController = require('../controllers/auth.controller');

//PUBLIC
//POST http://localhost:9002/api/auth/register
router.post("/register", AuthController.register);
//POST http://localhost:9002/api/auth/login
router.post("/login",AuthController.authentication);
//POST http://localhost:9000/api/auth/forgottenPassword
router.post("/forgottenPassword",AuthController.forgottenPassword);


module.exports = router;
