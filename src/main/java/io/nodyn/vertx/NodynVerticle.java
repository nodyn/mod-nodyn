package io.nodyn.vertx;

import io.nodyn.Nodyn;
import io.nodyn.runtime.NodynConfig;
import io.nodyn.runtime.RuntimeFactory;
import org.vertx.java.core.Future;
import org.vertx.java.platform.Verticle;

public class NodynVerticle extends Verticle {

    private Nodyn nodyn;

    @Override
    public void start(final Future<Void> startedResult) {
        RuntimeFactory factory = RuntimeFactory.init(getClass().getClassLoader(), RuntimeFactory.RuntimeType.DYNJS);

        final String main = container.config().getField( "main" );
        if ( main == null || "".equals( main ) ) {
            startedResult.setFailure( new IllegalArgumentException( "main cannot be empty" ) );
            return;
        }

        NodynConfig config = new NodynConfig( new String[] { main } );

        this.nodyn = factory.newRuntime( vertx, config );
        try {
            this.nodyn.run();
            startedResult.setResult(null);
        } catch (Throwable throwable) {
            startedResult.setFailure(throwable);
        }
    }

    @Override
    public void stop() {
        this.nodyn.reallyExit(0);
    }
}
