package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;

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

/** This is an auto generated class representing the Device type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Devices", authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", operations = { ModelOperation.READ })
})
@Index(name = "byVirtualNetwork", fields = {"virtualNetworkID"})
public final class Device implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField INSTANCE_ID = field("instanceID");
  public static final QueryField NAME = field("name");
  public static final QueryField USER_AGENT = field("userAgent");
  public static final QueryField DESCRIPTION = field("description");
//  public static final QueryField JOIN_STATUS = field("joinStatus");
//  public static final QueryField CONNECTION_STATUS = field("connectionStatus");
  public static final QueryField VIRTUAL_IP = field("virtualIP");
  public static final QueryField PUBLIC_KEY = field("publicKey");
  public static final QueryField VIRTUAL_NETWORK = field("virtualNetworkID");
  public static final QueryField USER = field("ownerID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String instanceID;
  private final @ModelField(targetType="String", authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", operations = { ModelOperation.UPDATE })
  }) String name;
  private final @ModelField(targetType="String", isRequired = true) String userAgent;
  private final @ModelField(targetType="String", authRules = {
    @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", operations = { ModelOperation.UPDATE })
  }) String description;
//  private final @ModelField(targetType="JoinStatus", isRequired = true) JoinStatus joinStatus;
//  private final @ModelField(targetType="ConnectionStatus", isRequired = true) ConnectionStatus connectionStatus;
  private final @ModelField(targetType="AWSIPAddress", authRules = {
    @AuthRule(allow = AuthStrategy.GROUPS, groupClaim = "cognito:groups", groups = { "Operator" }, operations = { ModelOperation.UPDATE })
  }) String virtualIP;
  private final @ModelField(targetType="String", authRules = {
    @AuthRule(allow = AuthStrategy.GROUPS, groupClaim = "cognito:groups", groups = { "Operator" }, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
  }) String publicKey;
  private final @ModelField(targetType="VirtualNetwork") @BelongsTo(targetName = "virtualNetworkID", type = VirtualNetwork.class) VirtualNetwork virtualNetwork;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "ownerID", type = User.class) User user;
  public String getId() {
      return id;
  }
  
  public String getInstanceId() {
      return instanceID;
  }
  
  public String getName() {
      return name;
  }
  
  public String getUserAgent() {
      return userAgent;
  }
  
  public String getDescription() {
      return description;
  }
  
//  public JoinStatus getJoinStatus() {
//      return joinStatus;
//  }
//
//  public ConnectionStatus getConnectionStatus() {
//      return connectionStatus;
//  }
  
  public String getVirtualIp() {
      return virtualIP;
  }
  
  public String getPublicKey() {
      return publicKey;
  }
  
  public VirtualNetwork getVirtualNetwork() {
      return virtualNetwork;
  }
  
  public User getUser() {
      return user;
  }
  
  private Device(String id, String instanceID, String name, String userAgent, String description/*, JoinStatus joinStatus, ConnectionStatus connectionStatus*/, String virtualIP, String publicKey, VirtualNetwork virtualNetwork, User user) {
    this.id = id;
    this.instanceID = instanceID;
    this.name = name;
    this.userAgent = userAgent;
    this.description = description;
//    this.joinStatus = joinStatus;
//    this.connectionStatus = connectionStatus;
    this.virtualIP = virtualIP;
    this.publicKey = publicKey;
    this.virtualNetwork = virtualNetwork;
    this.user = user;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Device device = (Device) obj;
      return ObjectsCompat.equals(getId(), device.getId()) &&
              ObjectsCompat.equals(getInstanceId(), device.getInstanceId()) &&
              ObjectsCompat.equals(getName(), device.getName()) &&
              ObjectsCompat.equals(getUserAgent(), device.getUserAgent()) &&
              ObjectsCompat.equals(getDescription(), device.getDescription()) &&
//              ObjectsCompat.equals(getJoinStatus(), device.getJoinStatus()) &&
//              ObjectsCompat.equals(getConnectionStatus(), device.getConnectionStatus()) &&
              ObjectsCompat.equals(getVirtualIp(), device.getVirtualIp()) &&
              ObjectsCompat.equals(getPublicKey(), device.getPublicKey()) &&
              ObjectsCompat.equals(getVirtualNetwork(), device.getVirtualNetwork()) &&
              ObjectsCompat.equals(getUser(), device.getUser());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getInstanceId())
      .append(getName())
      .append(getUserAgent())
      .append(getDescription())
//      .append(getJoinStatus())
//      .append(getConnectionStatus())
      .append(getVirtualIp())
      .append(getPublicKey())
      .append(getVirtualNetwork())
      .append(getUser())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Device {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("instanceID=" + String.valueOf(getInstanceId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("userAgent=" + String.valueOf(getUserAgent()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
//      .append("joinStatus=" + String.valueOf(getJoinStatus()) + ", ")
//      .append("connectionStatus=" + String.valueOf(getConnectionStatus()) + ", ")
      .append("virtualIP=" + String.valueOf(getVirtualIp()) + ", ")
      .append("publicKey=" + String.valueOf(getPublicKey()) + ", ")
      .append("virtualNetwork=" + String.valueOf(getVirtualNetwork()) + ", ")
      .append("user=" + String.valueOf(getUser()))
      .append("}")
      .toString();
  }
  
  public static InstanceIdStep builder() {
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
  public static Device justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Device(
      id,
      null,
      null,
      null,
//      null,
//      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      instanceID,
      name,
      userAgent,
      description,
//      joinStatus,
//      connectionStatus,
      virtualIP,
      publicKey,
      virtualNetwork,
      user);
  }
  public interface InstanceIdStep {
    UserAgentStep instanceId(String instanceId);
  }
  

  public interface UserAgentStep {
      BuildStep userAgent(String userAgent);
  }
  

  public interface JoinStatusStep {
    ConnectionStatusStep joinStatus(JoinStatus joinStatus);
  }
  

  public interface ConnectionStatusStep {
    BuildStep connectionStatus(ConnectionStatus connectionStatus);
  }
  

  public interface BuildStep {
    Device build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep name(String name);
    BuildStep description(String description);
    BuildStep virtualIp(String virtualIp);
    BuildStep publicKey(String publicKey);
    BuildStep virtualNetwork(VirtualNetwork virtualNetwork);
    BuildStep user(User user);
  }
  

  public static class Builder implements InstanceIdStep, UserAgentStep, JoinStatusStep, ConnectionStatusStep, BuildStep {
    private String id;
    private String instanceID;
    private String userAgent;
    private JoinStatus joinStatus;
    private ConnectionStatus connectionStatus;
    private String name;
    private String description;
    private String virtualIP;
    private String publicKey;
    private VirtualNetwork virtualNetwork;
    private User user;
    @Override
     public Device build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Device(
          id,
          instanceID,
          name,
          userAgent,
          description,
//          joinStatus,
//          connectionStatus,
          virtualIP,
          publicKey,
          virtualNetwork,
          user);
    }
    
    @Override
     public UserAgentStep instanceId(String instanceId) {
        Objects.requireNonNull(instanceId);
        this.instanceID = instanceId;
        return this;
    }
    
    @Override
     public BuildStep userAgent(String userAgent) {
        Objects.requireNonNull(userAgent);
        this.userAgent = userAgent;
        return this;
    }
    
    @Override
     public ConnectionStatusStep joinStatus(JoinStatus joinStatus) {
        Objects.requireNonNull(joinStatus);
        this.joinStatus = joinStatus;
        return this;
    }
    
    @Override
     public BuildStep connectionStatus(ConnectionStatus connectionStatus) {
        Objects.requireNonNull(connectionStatus);
        this.connectionStatus = connectionStatus;
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
    
    @Override
     public BuildStep virtualIp(String virtualIp) {
        this.virtualIP = virtualIp;
        return this;
    }
    
    @Override
     public BuildStep publicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }
    
    @Override
     public BuildStep virtualNetwork(VirtualNetwork virtualNetwork) {
        this.virtualNetwork = virtualNetwork;
        return this;
    }
    
    @Override
     public BuildStep user(User user) {
        this.user = user;
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
    private CopyOfBuilder(String id, String instanceId, String name, String userAgent, String description/*, JoinStatus joinStatus, ConnectionStatus connectionStatus*/, String virtualIp, String publicKey, VirtualNetwork virtualNetwork, User user) {
      super.id(id);
      super.instanceId(instanceId)
        .userAgent(userAgent)
//        .joinStatus(joinStatus)
//        .connectionStatus(connectionStatus)
        .name(name)
        .description(description)
        .virtualIp(virtualIp)
        .publicKey(publicKey)
        .virtualNetwork(virtualNetwork)
        .user(user);
    }
    
    @Override
     public CopyOfBuilder instanceId(String instanceId) {
      return (CopyOfBuilder) super.instanceId(instanceId);
    }
    
    @Override
     public CopyOfBuilder userAgent(String userAgent) {
      return (CopyOfBuilder) super.userAgent(userAgent);
    }
    
    @Override
     public CopyOfBuilder joinStatus(JoinStatus joinStatus) {
      return (CopyOfBuilder) super.joinStatus(joinStatus);
    }
    
    @Override
     public CopyOfBuilder connectionStatus(ConnectionStatus connectionStatus) {
      return (CopyOfBuilder) super.connectionStatus(connectionStatus);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder virtualIp(String virtualIp) {
      return (CopyOfBuilder) super.virtualIp(virtualIp);
    }
    
    @Override
     public CopyOfBuilder publicKey(String publicKey) {
      return (CopyOfBuilder) super.publicKey(publicKey);
    }
    
    @Override
     public CopyOfBuilder virtualNetwork(VirtualNetwork virtualNetwork) {
      return (CopyOfBuilder) super.virtualNetwork(virtualNetwork);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
  }
  
}
