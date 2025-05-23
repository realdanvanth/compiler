{
  description = "Java development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-23.11";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
        };
      in {
        devShells.default = pkgs.mkShell {
          name = "java-dev";
          buildInputs = [
            pkgs.openjdk17
            pkgs.maven # or gradle if you're using that
            pkgs.nasm
          ];
          shellHook = ''
            echo "☕ Java dev shell activated!"
          '';
        };
      });
}

