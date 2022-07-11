//Import modules
const express = require('express')

//Set required variables
const app = express()
const port = 80

//Make static files from "public" directory available on server
app.use(express.static('public'))
app.use('/css', express.static(__dirname + 'public/css'))
app.use('/js', express.static(__dirname + 'public/js'))
app.use('/img', express.static(__dirname + 'public/img'))

//Map HTML pages to HTTP requests
app.get('/', (req,res)=>{
    res.sendFile(__dirname + '/html/index.html')
})

//Start listening on port80
app.listen(port, () => console.info('Server is listening on port ' + port))