const router = require('express').Router();
const AuthController = require('../controllers/auth.controller');

//PUBLIC
//POST http://localhost:9000/api/auth/register
router.post("/register", AuthController.register);
//GET http://localhost:9000/api/auth/login
router.post("/login",AuthController.authentication);


module.exports = router;
