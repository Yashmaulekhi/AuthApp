
import { Button } from "./ui/button";
import { FaGithub, FaGoogle } from "react-icons/fa";


function OAuth2Buttons() {
  return (
    <div className="space-y-3">
      <a
        href={`${
          import.meta.env.VITE_BASE_URL || "http://localhost:8084"
        }/oauth2/authorization/google`}
        className={"block"}
      >
        <Button
          type="button"
          variant="outline"
          className="w-full cursor-pointer flex items-center gap-3 rounded-2xl"
        >
          <FaGoogle className="w-5 h-5" /> Continue with Google
        </Button>
      </a>

      <a
        href={`${
          import.meta.env.VITE_BASE_URL || "http://localhost:8084"
        }/oauth2/authorization/github`}
        className={"block"}
      >
        <Button
          type="button"
          variant="outline"
          className="w-full flex cursor-pointer items-center gap-3 rounded-2xl"
        >
          <FaGithub className="w-5 h-5" /> Continue with GitHub
        </Button>
      </a>
    </div>
  );
}

export default OAuth2Buttons;