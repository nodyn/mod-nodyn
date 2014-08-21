package io.nodyn.vertx;

import io.nodyn.Nodyn;
import io.nodyn.NodynConfig;
import org.vertx.java.core.Future;
import org.vertx.java.platform.Verticle;

public class NodynVerticle extends Verticle {

    private Nodyn nodyn;

    @Override
    public void start(final Future<Void> startedResult) {
        NodynConfig config = new NodynConfig( getClass().getClassLoader() );

        final String main = container.config().getField( "main" );
        if ( main == null || "".equals( main ) ) {
            startedResult.setFailure( new IllegalArgumentException( "main cannot be empty" ) );
            return;
        }

        config.setArgv( new String[] { main } );

        this.nodyn = new Nodyn(vertx, config);
        this.nodyn.start( new Runnable() {
            @Override
            public void run() {
                startedResult.setResult(null);
            }
        });
    }

    @Override
    public void stop() {
        try {
            this.nodyn.shutdown();
            this.nodyn.await();
        } catch (Throwable throwable) {
            container.logger().fatal("error stopping", throwable);
        }
    }
}
