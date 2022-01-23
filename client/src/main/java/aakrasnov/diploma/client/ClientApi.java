package aakrasnov.diploma.client;

import aakrasnov.diploma.client.filter.Filter;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Optional;

public interface ClientApi {

    Optional<JsonObject> document(long id);

    Collection<JsonObject> filteredDocuments(Filter filter);

    Collection<JsonObject> filteredDocuments(Collection<Filter> filters);

    void delete(long id);

    void add(JsonObject document);

    void update(long id, JsonObject altered);
}
