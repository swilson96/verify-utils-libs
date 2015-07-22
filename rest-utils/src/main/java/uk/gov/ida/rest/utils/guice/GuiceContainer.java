package uk.gov.ida.rest.utils.guice;

//public class GuiceContainer extends ServletContainer {
//
//    private static final long serialVersionUID = 1931878850157940335L;
//
//    @Inject
//    private Injector injector;
//
//    public static class ServletGuiceComponentProviderFactory extends GuiceComponentProviderFactory {
//        public ServletGuiceComponentProviderFactory(ResourceConfig config, Injector injector) {
//            super(config, injector);
//        }
//
//        @Override
//        public Map<Scope, ComponentScope> createScopeMap() {
//            Map<Scope, ComponentScope> m = super.createScopeMap();
//
//            m.put(ServletScopes.REQUEST, ComponentScope.PerRequest);
//            return m;
//        }
//    }
//
//    /**
//     * Creates a new container.
//     *
//     * @param app the JAX-RS application
//     */
//    public GuiceContainer(Application app) {
//        super(app);
//    }
//
//    @Override
//    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props,
//                                                      WebConfig webConfig) throws ServletException {
//        return new DefaultResourceConfig();
//    }
//
//    @Override
//    protected void initiate(ResourceConfig config, WebApplication webapp) {
//        webapp.initiate(config, new ServletGuiceComponentProviderFactory(config, injector));
//    }
//}
