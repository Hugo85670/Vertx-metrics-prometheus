package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.PrometheusScrapingHandler;
import io.vertx.micrometer.VertxPrometheusOptions;

public class MainVerticle extends AbstractVerticle
{

	@Override
	public void start(Future<Void> startFuture) throws Exception
	{
		int    port = 9100;
		String path = "/metrics";

		if (System.getenv("CC_METRICS_PROMETHEUS_PORT") != null)
			port = Integer.parseInt(System.getenv("CC_METRICS_PROMETHEUS_PORT"));

		if (System.getenv("CC_METRICS_PROMETHEUS_PATH") != null)
			path = System.getenv("CC_METRICS_PROMETHEUS_PATH");

		MicrometerMetricsOptions options = new MicrometerMetricsOptions()
			.setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
			.setEnabled(true);

		Vertx vertx2 = Vertx.vertx(new VertxOptions().setMetricsOptions(options));

		Router router = Router.router(vertx2);
		router.route(path).handler(PrometheusScrapingHandler.create());
		vertx2.createHttpServer().requestHandler(router).listen(port);

		// Deploy classic server
		vertx2.deployVerticle(ServerVerticle.class.getName(), result -> {
			if (result.succeeded()) {
				System.out.println("Success deploy");
			} else {
				System.out.println(result.cause());
			}
		});
	}
}
