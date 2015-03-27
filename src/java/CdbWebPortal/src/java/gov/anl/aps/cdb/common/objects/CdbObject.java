package gov.anl.aps.cdb.common.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import java.io.Serializable;

public class CdbObject implements Serializable {

    protected Long id = null;
    protected String name = null;
    protected String description = null;

    /**
     * Constructor.
     */
    public CdbObject() {}

    /**
     * Get object id.
     *
     * @return object id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set object id.
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get object name.
     *
     * @return object name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set object name.
     *
     * @param name object name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get object description.
     *
     * @return object description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set object description.
     *
     * @param description object description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get JSON representation.
     *
     * @return json string
     */
    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    /**
     * Encode object.
     *
     * @throws CdbException in case of any errors
     */
    public void encode() throws CdbException {}

    /**
     * Decode object.
     *
     * @throws CdbException in case of any errors
     */
    public void decode() throws CdbException {}
}    

