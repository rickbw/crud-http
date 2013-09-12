package rickbw.crud.http;

import rickbw.crud.Resource;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.UniformInterface;


/*package*/ abstract class AbstractJerseyResource<RESPONSE> implements Resource {

    private final UniformInterface resource;
    private final Class<? extends RESPONSE> responseClass;


    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [resource=" + getResource() +
                ", responseClass=" + getResponseClass() + ']';
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractJerseyResource<?> other = (AbstractJerseyResource<?>) obj;
        if (!getResource().equals(other.getResource())) {
            return false;
        }
        if (!getResponseClass().equals(other.getResponseClass())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getResource().hashCode();
        result = prime * result + getResponseClass().hashCode();
        return result;
    }

    protected AbstractJerseyResource(
            final UniformInterface resource,
            final Class<? extends RESPONSE> responseClass) {
        this.resource = Preconditions.checkNotNull(resource);
        this.responseClass = Preconditions.checkNotNull(responseClass);
    }

    protected final UniformInterface getResource() {
        return this.resource;
    }

    protected final Class<? extends RESPONSE> getResponseClass() {
        return this.responseClass;
    }

}
