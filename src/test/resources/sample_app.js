
var vertx = require( 'vertx2-core' );

vertx.eventbus.register( "sample.app", function(message) {
  console.log( "got a message!" );
  message.reply( "Howdy!" );
});

console.log( "registered handler" );
