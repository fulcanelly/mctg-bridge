var mc = require('minecraft-protocol');
var client = mc.createClient({
  host: "localhost",   // optional
 // port: 25565,         // optional
  username: "frr",
 // password: "12345678",
//  auth: 'mojang' // optional; by default uses mojang, if using a microsoft account, set to 'microsoft'
});


client.on('chat', function(packet) {
  // Listen for chat messages and echo them back.
  var jsonMsg = JSON.parse(packet.message);
  if(jsonMsg.translate == 'chat.type.announcement' || jsonMsg.translate == 'chat.type.text') {
    var username = jsonMsg.with[0].text;
    var msg = jsonMsg.with[1];
    if(username === client.username) return;
    client.write('chat', {message: msg.text});
  }
});
