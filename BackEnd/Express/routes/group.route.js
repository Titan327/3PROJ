const router = require('express').Router();
const GroupController = require('../controllers/group.controller');
const securityMiddleware = require('../security/middleware.security');

//PUBLIC
//POST http://localhost:9000/api/group/create
router.post("/create", securityMiddleware.verifyIsAuth, GroupController.createGroup);

module.exports = router;
