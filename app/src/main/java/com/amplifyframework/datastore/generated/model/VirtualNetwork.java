package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;

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

/** This is an auto generated class representing the VirtualNetwork type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "VirtualNetworks", authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", operations = { ModelOperation.READ }),
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "members", identityClaim = "cognito:username", operations = { ModelOperation.READ })
})
public final class VirtualNetwork implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField NAME = field("name");
  public static final QueryField DESCRIPTION = field("description");
  public static final QueryField COMMUNITY_NAME = field("communityName");
  public static final QueryField OWNER_ID = field("ownerID");
  public static final QueryField IP_PREFIX = field("ipPrefix");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", operations = { ModelOperation.UPDATE })
  }) String name;
  private final @ModelField(targetType="String", authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", operations = { ModelOperation.UPDATE })
  }) String description;
  private final @ModelField(targetType="String", isRequired = true) String communityName;
  private final @ModelField(targetType="ID", isRequired = true) String ownerID;
  private final @ModelField(targetType="UserVirtualNetwork") @HasMany(associatedWith = "virtualNetwork", type = UserVirtualNetwork.class) List<UserVirtualNetwork> members = null;
  private final @ModelField(targetType="String", isRequired = true) String ipPrefix;
  private final @ModelField(targetType="Device") @HasMany(associatedWith = "virtualNetwork", type = Device.class) List<Device> devices = null;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getDescription() {
      return description;
  }
  
  public String getCommunityName() {
      return communityName;
  }
  
  public String getOwnerId() {
      return ownerID;
  }
  
  public List<UserVirtualNetwork> getMembers() {
      return members;
  }
  
  public String getIpPrefix() {
      return ipPrefix;
  }
  
  public List<Device> getDevices() {
      return devices;
  }
  
  private VirtualNetwork(String id, String name, String description, String communityName, String ownerID, String ipPrefix) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.communityName = communityName;
    this.ownerID = ownerID;
    this.ipPrefix = ipPrefix;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      VirtualNetwork virtualNetwork = (VirtualNetwork) obj;
      return ObjectsCompat.equals(getId(), virtualNetwork.getId()) &&
              ObjectsCompat.equals(getName(), virtualNetwork.getName()) &&
              ObjectsCompat.equals(getDescription(), virtualNetwork.getDescription()) &&
              ObjectsCompat.equals(getCommunityName(), virtualNetwork.getCommunityName()) &&
              ObjectsCompat.equals(getOwnerId(), virtualNetwork.getOwnerId()) &&
              ObjectsCompat.equals(getIpPrefix(), virtualNetwork.getIpPrefix());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getDescription())
      .append(getCommunityName())
      .append(getOwnerId())
      .append(getIpPrefix())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("VirtualNetwork {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("communityName=" + String.valueOf(getCommunityName()) + ", ")
      .append("ownerID=" + String.valueOf(getOwnerId()) + ", ")
      .append("ipPrefix=" + String.valueOf(getIpPrefix()))
      .append("}")
      .toString();
  }
  
  public static CommunityNameStep builder() {
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
  public static VirtualNetwork justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new VirtualNetwork(
      id,
      null,
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
      communityName,
      ownerID,
      ipPrefix);
  }
  public interface CommunityNameStep {
    OwnerIdStep communityName(String communityName);
  }
  

  public interface OwnerIdStep {
    IpPrefixStep ownerId(String ownerId);
  }
  

  public interface IpPrefixStep {
    BuildStep ipPrefix(String ipPrefix);
  }
  

  public interface BuildStep {
    VirtualNetwork build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep name(String name);
    BuildStep description(String description);
  }
  

  public static class Builder implements CommunityNameStep, OwnerIdStep, IpPrefixStep, BuildStep {
    private String id;
    private String communityName;
    private String ownerID;
    private String ipPrefix;
    private String name;
    private String description;
    @Override
     public VirtualNetwork build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new VirtualNetwork(
          id,
          name,
          description,
          communityName,
          ownerID,
          ipPrefix);
    }
    
    @Override
     public OwnerIdStep communityName(String communityName) {
        Objects.requireNonNull(communityName);
        this.communityName = communityName;
        return this;
    }
    
    @Override
     public IpPrefixStep ownerId(String ownerId) {
        Objects.requireNonNull(ownerId);
        this.ownerID = ownerId;
        return this;
    }
    
    @Override
     public BuildStep ipPrefix(String ipPrefix) {
        Objects.requireNonNull(ipPrefix);
        this.ipPrefix = ipPrefix;
        return this;
    }
    
    @Override
     public BuildStep name(String name) {
        this.name = name;
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
    private CopyOfBuilder(String id, String name, String description, String communityName, String ownerId, String ipPrefix) {
      super.id(id);
      super.communityName(communityName)
        .ownerId(ownerId)
        .ipPrefix(ipPrefix)
        .name(name)
        .description(description);
    }
    
    @Override
     public CopyOfBuilder communityName(String communityName) {
      return (CopyOfBuilder) super.communityName(communityName);
    }
    
    @Override
     public CopyOfBuilder ownerId(String ownerId) {
      return (CopyOfBuilder) super.ownerId(ownerId);
    }
    
    @Override
     public CopyOfBuilder ipPrefix(String ipPrefix) {
      return (CopyOfBuilder) super.ipPrefix(ipPrefix);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
  }
  
}
