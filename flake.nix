{
  inputs = {
    typelevel-nix.url = "github:typelevel/typelevel-nix";
    nixpkgs.follows = "typelevel-nix/nixpkgs";
    flake-utils.follows = "typelevel-nix/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
      typelevel-nix,
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ typelevel-nix.overlays.default ];
        };
      in
      {
        devShell = pkgs.devshell.mkShell {
          packages = with pkgs; [
            iverilog
            gcc
            boost
          ];

          imports = [ typelevel-nix.typelevelShell ];
          name = "core";
          typelevelShell = {
            jdk.package = pkgs.jdk23;
            native.enable = false;
            nodejs.enable = true;
          };
        };
      }
    );
}
