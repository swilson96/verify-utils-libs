package uk.gov.ida.rest.utils.guice;

import com.sun.jersey.guice.JerseyServletModule;

public class JerseyModule extends JerseyServletModule {
    private final GuiceContainer container;
    /**
     * Creates a new JerseyModule.
     * <p/>
     * @param container a container binding Jersey to Guice
     */
    public JerseyModule(GuiceContainer container)
    {
        this.container = container;
    }

    @Override
    protected void configureServlets()
    {
        bind(GuiceContainer.class).toInstance(container);
    }
}
