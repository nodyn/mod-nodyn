
var eventbus = require('./eventbus');

var handle = eventbus.register('sample.app', function(message) {
  message.reply('Howdy!');
});

console.log('registered handler');
