package com.developerali.lshistutorial.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CouponModel implements Parcelable {
    String code, percentage, id;
    Long valid;

    public CouponModel(String code, String percentage, Long valid) {
        this.code = code;
        this.percentage = percentage;
        this.valid = valid;
    }
    public CouponModel() {
    }

    protected CouponModel(Parcel in) {
        code = in.readString();
        percentage = in.readString();
        id = in.readString();
        if (in.readByte() == 0) {
            valid = null;
        } else {
            valid = in.readLong();
        }
    }

    public static final Creator<CouponModel> CREATOR = new Creator<CouponModel>() {
        @Override
        public CouponModel createFromParcel(Parcel in) {
            return new CouponModel(in);
        }

        @Override
        public CouponModel[] newArray(int size) {
            return new CouponModel[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public Long getValid() {
        return valid;
    }

    public void setValid(Long valid) {
        this.valid = valid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(percentage);
        dest.writeString(id);
        if (valid == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(valid);
        }
    }
}
