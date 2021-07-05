package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the OmniWorker type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "OmniWorkers", authRules = {
  @AuthRule(allow = AuthStrategy.GROUPS, groupClaim = "cognito:groups", groups = { "Operator" }, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ }),
  @AuthRule(allow = AuthStrategy.PRIVATE, operations = { ModelOperation.READ })
})
public final class OmniWorker implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField NAME = field("name");
  public static final QueryField DESCRIPTION = field("description");
  public static final QueryField ADDR = field("addr");
  public static final QueryField PUBLIC_KEY = field("publicKey");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String") String description;
  private final @ModelField(targetType="AWSIPAddress", isRequired = true) String addr;
  private final @ModelField(targetType="String", isRequired = true) String publicKey;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getDescription() {
      return description;
  }
  
  public String getAddr() {
      return addr;
  }
  
  public String getPublicKey() {
      return publicKey;
  }
  
  private OmniWorker(String id, String name, String description, String addr, String publicKey) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.addr = addr;
    this.publicKey = publicKey;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      OmniWorker omniWorker = (OmniWorker) obj;
      return ObjectsCompat.equals(getId(), omniWorker.getId()) &&
              ObjectsCompat.equals(getName(), omniWorker.getName()) &&
              ObjectsCompat.equals(getDescription(), omniWorker.getDescription()) &&
              ObjectsCompat.equals(getAddr(), omniWorker.getAddr()) &&
              ObjectsCompat.equals(getPublicKey(), omniWorker.getPublicKey());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getDescription())
      .append(getAddr())
      .append(getPublicKey())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("OmniWorker {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("addr=" + String.valueOf(getAddr()) + ", ")
      .append("publicKey=" + String.valueOf(getPublicKey()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static OmniWorker justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new OmniWorker(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      description,
      addr,
      publicKey);
  }
  public interface NameStep {
    AddrStep name(String name);
  }
  

  public interface AddrStep {
    PublicKeyStep addr(String addr);
  }
  

  public interface PublicKeyStep {
    BuildStep publicKey(String publicKey);
  }
  

  public interface BuildStep {
    OmniWorker build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep description(String description);
  }
  

  public static class Builder implements NameStep, AddrStep, PublicKeyStep, BuildStep {
    private String id;
    private String name;
    private String addr;
    private String publicKey;
    private String description;
    @Override
     public OmniWorker build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new OmniWorker(
          id,
          name,
          description,
          addr,
          publicKey);
    }
    
    @Override
     public AddrStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public PublicKeyStep addr(String addr) {
        Objects.requireNonNull(addr);
        this.addr = addr;
        return this;
    }
    
    @Override
     public BuildStep publicKey(String publicKey) {
        Objects.requireNonNull(publicKey);
        this.publicKey = publicKey;
        return this;
    }
    
    @Override
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String description, String addr, String publicKey) {
      super.id(id);
      super.name(name)
        .addr(addr)
        .publicKey(publicKey)
        .description(description);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder addr(String addr) {
      return (CopyOfBuilder) super.addr(addr);
    }
    
    @Override
     public CopyOfBuilder publicKey(String publicKey) {
      return (CopyOfBuilder) super.publicKey(publicKey);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
  }
  
}
