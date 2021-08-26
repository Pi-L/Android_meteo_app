package info.legeay.meteo.util;

import android.content.Context;
import android.content.SharedPreferences;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Permission {

    private Context context;
    private String name;
    private PermissionEnum status;
    private SharedPreferences preferences;

    public Permission(Context context, String name) {
        this.context = context;
        this.name = name;

        this.preferences = this.context.getSharedPreferences("meteo_permissions", Context.MODE_PRIVATE);

        switch (this.preferences.getString(this.name, "unset")) {
            case "accepted":
                this.status = PermissionEnum.ACCEPTED;
                break;
            case "refused":
                this.status = PermissionEnum.REFUSED;
                break;
            default:
                this.status = PermissionEnum.UNSET;
                break;
        }
    }

    public void setStatus(PermissionEnum permissionEnum) {
        this.status = permissionEnum;
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putString(this.name, permissionEnum.getStatus());
        edit.apply();
    }

    public boolean isUnset() {
        return PermissionEnum.UNSET.equals(this.status);
    }

    public boolean isRefused() {
        return PermissionEnum.REFUSED.equals(this.status);
    }

    @Getter
    @AllArgsConstructor
    public enum PermissionEnum {
        UNSET("unset"),
        ACCEPTED("accepted"),
        REFUSED("refused");

        private String status;
    }
}
