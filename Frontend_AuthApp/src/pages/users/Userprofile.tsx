
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Avatar,
  AvatarFallback,
  AvatarImage,
} from "@/components/ui/avatar";
import { motion } from "framer-motion";
import useAuth from "@/auth/store";
import { useEffect, useState } from "react";

function Userprofile() {
  const [isEditing, setIsEditing] = useState(false);
  const [name, setName] = useState("");

  const user = useAuth((state) => state.user);

  // Set local name when user data is available
  useEffect(() => {
    if (user) {
      setName(user.name || "");
    }
  }, [user]);

  const handleCancel = () => {
    setName(user?.name || "");
    setIsEditing(false);
  };

  const handleSave = () => {
    console.log("Updated name:", name);

    // TODO:
    // Call your backend update API here

    setIsEditing(false);
  };

  return (
    <div className="p-6 max-w-3xl mx-auto space-y-8">

      {/* Heading */}
      <motion.h1
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-3xl font-bold text-center"
      >
        User Profile
      </motion.h1>

      {/* Profile Card */}
      <Card className="rounded-2xl shadow-md p-6">
        <CardHeader>
          <CardTitle className="text-xl font-semibold">
            Profile Information
          </CardTitle>
        </CardHeader>

        <CardContent className="space-y-6">

          {/* Avatar */}
          <div className="flex flex-col items-center gap-3">
            <Avatar className="w-28 h-28 border shadow-md">
              <AvatarImage
                src="https://api.dicebear.com/7.x/thumbs/svg?seed=user"
                alt="User profile"
              />

              <AvatarFallback>
                {user?.name?.charAt(0)?.toUpperCase() || "U"}
              </AvatarFallback>
            </Avatar>

            <Button
              variant="outline"
              className="rounded-xl px-5"
            >
              Change Picture
            </Button>
          </div>

          {/* User Details */}
          {!isEditing ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

              {/* Name */}
              <div className="space-y-2">
                <Label htmlFor="name">Full Name</Label>

                <Input
                  id="name"
                  value={user?.name || ""}
                  readOnly
                  className="rounded-xl"
                />
              </div>

              {/* Email */}
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>

                <Input
                  id="email"
                  value={user?.email || ""}
                  readOnly
                  className="rounded-xl"
                />
              </div>

              {/* Provider */}
              <div className="space-y-2">
                <Label htmlFor="provider">Provider</Label>

                <Input
                  id="provider"
                  value={user?.provider || ""}
                  readOnly
                  className="rounded-xl"
                />
              </div>

              {/* Enabled */}
              <div className="space-y-2">
                <Label htmlFor="enabled">Enabled</Label>

                <Input
                  id="enabled"
                  value={user?.enabled ? "Yes" : "No"}
                  readOnly
                  className="rounded-xl"
                />
              </div>

            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

              {/* Editable Name */}
              <div className="space-y-2">
                <Label htmlFor="name">Full Name</Label>

                <Input
                  id="name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="rounded-xl"
                />
              </div>

              {/* Email */}
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>

                <Input
                  id="email"
                  value={user?.email || ""}
                  readOnly
                  className="rounded-xl"
                />
              </div>

              {/* Provider */}
              <div className="space-y-2">
                <Label htmlFor="provider">Provider</Label>

                <Input
                  id="provider"
                  value={user?.provider || ""}
                  readOnly
                  className="rounded-xl"
                />
              </div>

              {/* Enabled */}
              <div className="space-y-2">
                <Label htmlFor="enabled">Enabled</Label>

                <Input
                  id="enabled"
                  value={user?.enabled ? "Yes" : "No"}
                  readOnly
                  className="rounded-xl"
                />
              </div>

            </div>
          )}

          {/* Buttons */}
          {!isEditing ? (
            <Button
              onClick={() => setIsEditing(true)}
              className="w-full rounded-2xl mt-4 text-lg"
            >
              Edit Profile
            </Button>
          ) : (
            <div className="flex gap-3 mt-4">

              {/* Cancel */}
              <Button
                variant="outline"
                className="rounded-2xl w-full"
                onClick={handleCancel}
              >
                Cancel
              </Button>

              {/* Save */}
              <Button
                className="rounded-2xl w-full"
                onClick={handleSave}
              >
                Save
              </Button>

            </div>
          )}

        </CardContent>
      </Card>

      {/* Account Settings */}
      <Card className="rounded-2xl shadow-md p-6">
        <CardHeader>
          <CardTitle className="text-xl">
            Account Settings
          </CardTitle>
        </CardHeader>

        <CardContent className="space-y-4">

          {/* Change Password */}
          <Button
            variant="outline"
            className="w-full rounded-xl py-3 text-base"
          >
            Change Password
          </Button>

          {/* Delete Account */}
          <Button
            variant="destructive"
            className="w-full rounded-xl py-3 text-base"
          >
            Delete Account
          </Button>

        </CardContent>
      </Card>

    </div>
  );
}

export default Userprofile;
